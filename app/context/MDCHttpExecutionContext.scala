package context

import org.slf4j.MDC

import scala.concurrent.{ExecutionContextExecutor, ExecutionContext}

// from https://yanns.github.io/blog/2014/05/04/slf4j-mapped-diagnostic-context-mdc-with-play-framework/
// solution 2, custom execution context
// however, i had to remove the existential types because the MDC context map is Map<String, String>

/**
 * slf4j provides a MDC [[http://logback.qos.ch/manual/mdc.html Mapped Diagnostic Context]]
 * based on a [[ThreadLocal]]. In an asynchronous environment, the callbacks can be called
 * in another thread, where the local thread variable does not exist anymore.
 *
 * This execution context fixes this problem:
 * it propagates the MDC from the caller's thread to the callee's one.
 */
object MDCHttpExecutionContext {

  /**
   * Create an MDCHttpExecutionContext with values from the current thread.
   */
  def fromThread(delegate: ExecutionContext): ExecutionContextExecutor = {
    new MDCHttpExecutionContext(MDC.getCopyOfContextMap, delegate)
  }
}

/**
 * Manages execution to ensure that the given MDC context are set correctly in the current thread.
 * Actual execution is performed by a delegate ExecutionContext.
 */
class MDCHttpExecutionContext(mdcContext: java.util.Map[String,String], delegate: ExecutionContext)
  extends ExecutionContextExecutor {

  override def execute(runnable: Runnable) = delegate.execute(
    new Runnable {
      override def run() = {
        val oldMdcContext = MDC.getCopyOfContextMap
        setContextMap(mdcContext)
        try {
          runnable.run()
        } finally {
          setContextMap(oldMdcContext)
        }
      }
    }
  )

  override def reportFailure(cause: Throwable) = delegate.reportFailure(cause)

  private def setContextMap(mdcContext: java.util.Map[String,String]) = {
    if (mdcContext ==  null) {
      MDC.clear()
    } else {
      MDC.setContextMap(mdcContext)
    }
  }

}
