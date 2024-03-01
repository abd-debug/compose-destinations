package com.ramcosta.playground.featurey

import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.annotation.ExternalNavGraph
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.generated.subfeaturey.navgraphs.SubFeatureYNavGraph

@NavGraph<ExternalModuleGraph>
internal annotation class FeatureYGraph {
    @ExternalNavGraph<SubFeatureYNavGraph>
    companion object Includes
}
