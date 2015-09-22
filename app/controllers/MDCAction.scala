package controllers

import context.Execution
import play.api.mvc.{Result, Request, ActionBuilder}

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by paullawler on 8/9/15.
 */
object MDCAction extends ActionBuilder[Request] {

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    block(request)
  }

  /**
   * The standard [[play.api.mvc.Action]] loses the MDC context.
   *
   * This action builder sets the [[ExecutionContext]] so that the
   * MDC context is propagated.
   * With this custom [[ExecutionContext]], the request and the correlation IDs
   * can be logged.
   */
  override def executionContext: ExecutionContext = Execution.defaultContext

}
