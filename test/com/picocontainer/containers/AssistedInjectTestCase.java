package com.picocontainer.containers;

import com.picocontainer.DefaultPicoContainer;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;


public class AssistedInjectTestCase {

    @Test
    public void testAssisted() {
        DefaultPicoContainer pc = new DefaultPicoContainer();
        pc.addComponent(Dep.class);
        pc.addAssistedComponent(TestFactory.class);
        TestFactory testFactory = pc.getComponent(TestFactory.class);

        TestComp testComp = testFactory.create("testName", "aaa");
        Assert.assertNotNull(testComp.dep);

    }


    interface TestFactory {
        TestComp create(String name, String data);
    }

     static class TestComp {
        @Inject
        Dep dep;

        String name, data;

         TestComp(String name, String data) {
            this.name = name;
            this.data = data;
        }
    }

    static class Dep {
    }
}
