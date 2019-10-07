package io.kotless.gen.factory.info

import io.kotless.Webapp
import io.kotless.gen.*
import io.kotless.hcl.ref
import io.kotless.terraform.provider.aws.data.info.caller_identity
import io.kotless.terraform.provider.aws.data.info.region
import io.kotless.terraform.provider.aws.data.s3.s3_bucket

object InfoFactory : GenerationFactory<Webapp, InfoFactory.InfoOutput> {
    data class InfoOutput(val account_id: String, val region_name: String, val kotless_bucket_arn: String)

    override fun mayRun(entity: Webapp, context: GenerationContext) = true

    override fun generate(entity: Webapp, context: GenerationContext): GenerationFactory.GenerationResult<InfoOutput> {
        val caller_identity = caller_identity("current") {}
        val region = region("current") {}
        val kotless_bucket = s3_bucket(Names.tf("kotless", "bucket")) {
            bucket = context.schema.kotlessConfig.bucket
        }

        return GenerationFactory.GenerationResult(InfoOutput(caller_identity::account_id.ref, region::name.ref, kotless_bucket::arn.ref),
            caller_identity, region, kotless_bucket)
    }
}