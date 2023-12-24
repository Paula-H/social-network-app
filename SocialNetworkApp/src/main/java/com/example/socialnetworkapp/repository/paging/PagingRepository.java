package com.example.socialnetworkapp.repository.paging;


import com.example.socialnetworkapp.domain.Entity;
import com.example.socialnetworkapp.repository.Repository;

public interface PagingRepository<ID,
        E extends Entity<ID>>
        extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);
}
