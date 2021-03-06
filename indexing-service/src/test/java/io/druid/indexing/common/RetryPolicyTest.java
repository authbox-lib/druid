/*
 * Druid - a distributed column store.
 * Copyright 2012 - 2015 Metamarkets Group Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.druid.indexing.common;

import junit.framework.Assert;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.junit.Test;

/**
 */
public class RetryPolicyTest
{
  @Test
  public void testGetAndIncrementRetryDelay() throws Exception
  {
    RetryPolicy retryPolicy = new RetryPolicy(
        new RetryPolicyConfig()
            .setMinWait(new Period("PT1S"))
            .setMaxWait(new Period("PT10S"))
            .setMaxRetryCount(6)
    );

    Assert.assertEquals(new Duration("PT1S"), retryPolicy.getAndIncrementRetryDelay());
    Assert.assertEquals(new Duration("PT2S"), retryPolicy.getAndIncrementRetryDelay());
    Assert.assertEquals(new Duration("PT4S"), retryPolicy.getAndIncrementRetryDelay());
    Assert.assertEquals(new Duration("PT8S"), retryPolicy.getAndIncrementRetryDelay());
    Assert.assertEquals(new Duration("PT10S"), retryPolicy.getAndIncrementRetryDelay());
    Assert.assertEquals(new Duration("PT10S"), retryPolicy.getAndIncrementRetryDelay());
    Assert.assertEquals(null, retryPolicy.getAndIncrementRetryDelay());
    Assert.assertTrue(retryPolicy.hasExceededRetryThreshold());
  }
}
