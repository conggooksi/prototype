package com.secondwind.prototype.common.spec;

import com.secondwind.prototype.common.exception.ApiException;

public abstract class AbstractSpecification<T> implements Specification<T> {

    @Override
    public abstract boolean isSatisfiedBy(T t);

    public abstract void check(T t) throws ApiException;

    @Override
    public Specification<T> and(Specification<T> specification) {
        return new AndSpecification<T>(this, specification);
    }
}
