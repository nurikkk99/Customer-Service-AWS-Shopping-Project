package com.myorg;

import java.util.List;
import java.util.Map;
import software.amazon.awscdk.CfnParameter;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.SecretValue;
import software.amazon.awscdk.SecretsManagerSecretOptions;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Port;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.ecs.AddCapacityOptions;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.Secret;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedEc2Service;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticsearch.AdvancedSecurityOptions;
import software.amazon.awscdk.services.elasticsearch.CapacityConfig;
import software.amazon.awscdk.services.elasticsearch.Domain;
import software.amazon.awscdk.services.elasticsearch.ElasticsearchVersion;
import software.amazon.awscdk.services.elasticsearch.EncryptionAtRestOptions;
import software.amazon.awscdk.services.iam.AnyPrincipal;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.IPrincipal;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.DatabaseSecret;
import software.amazon.awscdk.services.rds.PostgresEngineVersion;
import software.amazon.awscdk.services.rds.PostgresInstanceEngineProps;
import software.amazon.awscdk.services.rds.StorageType;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class HelloCdkStack extends Stack {
    public HelloCdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public HelloCdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        //Parameters
        CfnParameter ecrRepositoryName = CfnParameter.Builder.create(this, "ecrRepositoryName")
                .description("Name of repository ECR").build();

        CfnParameter imageTag = CfnParameter.Builder.create(this, "imageTag")
                .description("Tag of image").build();

        CfnParameter postgresUsername = CfnParameter.Builder.create(this, "postgresUsername")
                .defaultValue("postgres")
                .build();

        CfnParameter elasticUsername = CfnParameter.Builder.create(this, "elasticUsername")
                .defaultValue("elastic")
                .build();

        DatabaseSecret elasticDatabaseSecret = DatabaseSecret.Builder.create(this, "elasticSecret")
                .username(elasticUsername.getValueAsString())
                .build();

        SecretsManagerSecretOptions secretsManagerSecretOptions = SecretsManagerSecretOptions.builder()
                .jsonField("password").build();

        AdvancedSecurityOptions advancedSecurityOptions = AdvancedSecurityOptions.builder()
                .masterUserName(elasticUsername.getValueAsString())
                .masterUserPassword(SecretValue.secretsManager(elasticDatabaseSecret.getSecretArn(), secretsManagerSecretOptions))
                .build();

        EncryptionAtRestOptions.builder().enabled(true).build();

        IPrincipal principal = new AnyPrincipal();

        PolicyStatement policyStatement = PolicyStatement.Builder.create()
                .actions(List.of("es:*"))
                .effect(Effect.ALLOW)
                .principals(List.of(principal))
                .resources(List.of("arn:aws:es:eu-west-2:356106348453:domain/product-domain/*"))
                .build();

        Domain domain = Domain.Builder.create(this, "domain")
                .domainName("product-domain")
                .version(ElasticsearchVersion.V7_10)
                .fineGrainedAccessControl(advancedSecurityOptions)
                .encryptionAtRest(
                        EncryptionAtRestOptions.builder().enabled(true).build())
                .nodeToNodeEncryption(true)
                .enforceHttps(true)
                .capacity(CapacityConfig.builder()
                        .dataNodeInstanceType("t3.small.elasticsearch")
                        .dataNodes(2)
                        .masterNodes(0)
                        .build())
                .accessPolicies(List.of(policyStatement))
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        Vpc vpc = Vpc.Builder.create(this, "VPC")
                .vpcName("MyVpc2")
                .subnetConfiguration(Vpc.DEFAULT_SUBNETS_NO_NAT)
                .maxAzs(3)
                .build();

        DatabaseSecret postgresDatabaseSecret = DatabaseSecret.Builder.create(this, "postgresSecret")
                .username(postgresUsername.getValueAsString())
                .build();

        DatabaseInstance postgres = DatabaseInstance.Builder.create(this, "Postgres")
                .vpc(vpc)
                .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PUBLIC).build())
                .engine(
                        DatabaseInstanceEngine.postgres(
                                PostgresInstanceEngineProps.builder()
                                        .version(PostgresEngineVersion.VER_12_8)
                                        .build()
                        )
                )
                .credentials(Credentials.fromSecret(postgresDatabaseSecret))
                .databaseName("customerDatabase")
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .storageType(StorageType.GP2)
                .publiclyAccessible(true)
                .iamAuthentication(false)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        postgres.getConnections().allowFromAnyIpv4(Port.tcp(5432), "Allow all connections to the port 5432");

        Cluster cluster = Cluster.Builder.create(this, "customer-cluster")
                .vpc(vpc)
                .capacity(
                        AddCapacityOptions.builder()
                                .desiredCapacity(1)
                                .instanceType(InstanceType.of(InstanceClass.BURSTABLE3, InstanceSize.MICRO))
                                .associatePublicIpAddress(true)
                                .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PUBLIC).build())
                                .build())
                .build();

        IRole serviceTaskRole = Role.Builder.create(this, "ServiceTaskRole")
                .assumedBy(ServicePrincipal.Builder.create("ecs-tasks.amazonaws.com").build()).build();

        ApplicationLoadBalancedEc2Service.Builder.create(this, "customer-shopping-service")
                .cluster(cluster)
                .publicLoadBalancer(true)
                .healthCheckGracePeriod(Duration.hours(4))
                .desiredCount(1)
                .cpu(512)
                .memoryLimitMiB(700)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .containerName("shopping-customer")
                                .taskRole(serviceTaskRole)
                                .environment(Map.of(
                                        "POSTGRES_USER", postgresUsername.getValueAsString(),
                                        "POSTGRES_HOST",postgres.getDbInstanceEndpointAddress(),
                                        "POSTGRES_PORT",postgres.getDbInstanceEndpointPort(),
                                        "ELASTIC_USERNAME", advancedSecurityOptions.getMasterUserName(),
                                        "ELASTIC_URI", domain.getDomainEndpoint(),
                                        "RABBIT_ENDPOINT", StringParameter.valueForStringParameter(this, "brokersEndpointSSM"),
                                        "RABBIT_USERNAME", StringParameter.valueForStringParameter(this, "brokersUsernameSSM"),
                                        "RABBIT_PASSWORD", StringParameter.valueForStringParameter(this, "brokersPasswordSSM")
                                ))
                                .secrets(Map.of(
                                        "POSTGRES_PASSWORD", Secret.fromSecretsManager(postgresDatabaseSecret, "password"),
                                        "ELASTIC_PASSWORD", Secret.fromSecretsManager(elasticDatabaseSecret, "password")
                                ))
                                .image(
                                        ContainerImage.fromEcrRepository(
                                                Repository.fromRepositoryName(this, "EcrRepository", ecrRepositoryName.getValueAsString()),
                                                imageTag.getValueAsString()
                                        )
                                )
                                .containerPort(8082)
                                .build()
                )
                .build();
    }
}
