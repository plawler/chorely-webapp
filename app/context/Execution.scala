package context

import scala.concurrent.ExecutionContext


/**
 * The standard [[play.api.libs.concurrent.Execution.defaultContext]] loses the MDC context.
 *
 * This custom [[ExecutionContext]] propagates the MDC context, so that the request
 * and the correlation IDs can be logged.
 */
object Execution {

  object Implicits {
    implicit def defaultContext: ExecutionContext = Execution.defaultContext
  }

  def defaultContext: ExecutionContext = MDCHttpExecutionContext.fromThread(play.api.libs.concurrent.Execution.defaultContext)
}

