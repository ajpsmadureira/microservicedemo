package com.crm.mapper;

public interface Mapper<T,A> {

    A map(T t);
}