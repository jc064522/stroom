/*
 * Copyright 2016 Crown Copyright
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

package stroom.util.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import stroom.util.test.StroomJUnit4ClassRunner;

@RunWith(StroomJUnit4ClassRunner.class)
public class TestParameter {
    @Test
    public void testNodeName() {
        Parameter parameter = new Parameter();
        parameter.setRegEx("[a-zA-Z0-9-_]+");

        parameter.setValue("");
        Assert.assertFalse(parameter.validate());

        parameter.setValue("bad.value");
        Assert.assertFalse(parameter.validate());

        parameter.setValue("goodValue");
        Assert.assertTrue(parameter.validate());

        parameter.setValue("goodValue123");
        Assert.assertTrue(parameter.validate());

        parameter.setValue("UNUSUAL-123_goodValue");
        Assert.assertTrue(parameter.validate());

    }

    @Test
    public void testPortPrefix() {
        Parameter parameter = new Parameter();
        parameter.setRegEx("[0-9]{2}");

        parameter.setValue("1");
        Assert.assertFalse(parameter.validate());

        parameter.setValue("999");
        Assert.assertFalse(parameter.validate());

        parameter.setValue("12");
        Assert.assertTrue(parameter.validate());

        parameter.setValue("99");
        Assert.assertTrue(parameter.validate());

    }

}
