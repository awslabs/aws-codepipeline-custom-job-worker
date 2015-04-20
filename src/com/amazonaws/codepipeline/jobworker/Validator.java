package com.amazonaws.codepipeline.jobworker;

/**
 * Simple validation helper class.
 */
public final class Validator {

    /**
     * Validates that the given object is not null.
     * @param obj the given object
     * @throws java.lang.IllegalArgumentException thrown if given object is null
     */
    public final static void notNull(final Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Object is null");
        }
    }
}
