package com.example.socialnetworkapp.repository.paging;
import java.util.stream.Stream;

public interface Page<E> {
    Pageable getPageable();

    Pageable nextPageable();

    Pageable lastPageable();

    Stream<E> getContent();


}
