package com.ing.roomregistry.filters

import akka.stream.Materializer
import com.google.inject.Inject
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class LoggingFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {
  private val logger = Logger(LoggerFactory.getLogger("LoggingFilter"))

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    logger.info(s"Incoming request: ${requestHeader.method} ${requestHeader.uri}")
    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>

      val requestTime = System.currentTimeMillis - startTime
      logger.info(s"${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned ${result.header.status}")
      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}
