package com.auctions.mapper;

// TODO: evaluate ModelMapper or MapStruct to map objects

public interface Mapper<T,A> {

    A map(T t);
}