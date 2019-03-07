/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.matchers;

import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.collection.IsIterableContainingInOrder;
import weka.core.Instance;

/**
 *
 * @author María Novo
 */
public class IsEqualToInstance extends TypeSafeMatcher<Instance> {
    private Instance expected;
    
    private String mismatchMessage;
    
    public IsEqualToInstance(Instance expected) {
        this.expected = expected;
        this.mismatchMessage = null;
    }

    @Override
    protected boolean matchesSafely(Instance actual) {
        this.mismatchMessage = null;
        
        for (int i = 0; i < this.expected.numAttributes(); i++) {
            if (!this.expected.attribute(i).equals(actual.attribute(i))) {
                this.mismatchMessage = "";
                return false;
            }
            if (this.expected.value(i) != actual.value(i)) {
                this.mismatchMessage = "Value does not match on index " + i  ;
                return false;
            }
        }
        
        return true;
    }

    @Override
    public void describeTo(Description description) {
        if (this.mismatchMessage != null) {
            description.appendText(this.mismatchMessage);
        }
    }
    
    public static IsEqualToInstance equalToInstance(Instance instance) {
        return new IsEqualToInstance(instance);
    }
    
    public static Matcher<Iterable<? extends Instance>> containsInstancesInOrder(Instance... instances) {
        return IsEqualToInstance.containsInstancesInOrder(asList(instances));
    }
    
    public static Matcher<Iterable<? extends Instance>> containsInstancesInOrder(Collection<Instance> instances) {
        List<Matcher<? super Instance>> matchers = instances.stream()
            .map(IsEqualToInstance::equalToInstance)
        .collect(toList());
        
        return IsIterableContainingInOrder.contains(matchers);
    }
}
