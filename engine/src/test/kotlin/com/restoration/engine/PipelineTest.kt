package com.restoration.engine
import com.restoration.engine.domain.*
import com.restoration.engine.planner.RuleBasedPipelinePlanner
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class PipelineTest {
    @Test fun `planner adds deblock for high artifacts`() {
        val planner = RuleBasedPipelinePlanner()
        val q = ImageQualityProfile(1000, 1000, 0.5f, 0.5f, 0.3f, 0.9f, 0)
        val r = RestoreRequest(input = ImageInput(path = "test.jpg"))
        val p = planner.plan(r, q)
        assertTrue("DEBLOCK" in p.stages)
    }
    @Test fun `planner skips SR for high-res`() {
        val planner = RuleBasedPipelinePlanner()
        val q = ImageQualityProfile(4000, 3000, 0.9f, 0.1f, 0.1f, 0.1f, 0)
        val r = RestoreRequest(input = ImageInput(path = "test.jpg"))
        val p = planner.plan(r, q)
        assertTrue("SUPER_RESOLUTION" !in p.stages)
    }
    @Test fun `pipeline emits progress and completes`() = runTest {
        val pipeline = com.restoration.engine.pipeline.RestorationPipeline()
        val r = RestoreRequest(input = ImageInput(path = "test.jpg"))
        val events = pipeline.execute(r).toList()
        assertTrue(events.isNotEmpty())
        val last = events.last()
        assertTrue(last is RestoreProgress.Completed)
        assertNotNull((last as RestoreProgress.Completed).result.getOrNull())
    }
}
