package com.yunext.kmp.resource

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated

class MySymbolProcessor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {
    private val codeGenerator: CodeGenerator = environment.codeGenerator
    private val logger: KSPLogger = environment.logger
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("MySymbolProcessor::process")
        resolver.getAllFiles().forEach {
            logger.warn("--hd-->${it.fileName}")
        }
        return emptyList()
    }
}