package com.github.blemale.scaffeine

import java.util.concurrent.atomic.AtomicInteger

import com.github.benmanes.caffeine.cache.RemovalCause
import org.scalatest._

class RemovalListenerSpec
    extends WordSpec
    with Matchers {

  class StubListener extends ((String, String, RemovalCause) => Unit) {
    val callCounter = new AtomicInteger

    override def apply(key: String, value: String, cause: RemovalCause): Unit =
      callCounter.incrementAndGet()
  }

  "Cache" should {
    "call removal listener on enties eviction" in {
      val listener = new StubListener
      val cache =
        Scaffeine()
          .executor(DirectExecutor)
          .removalListener(listener)
          .build[String, String]()

      cache.put("foo", "bar")
      cache.invalidate("foo")

      listener.callCounter.get should be(1)
    }
  }

}
