package com.caiotayota.platform.repository;

import com.caiotayota.platform.entity.CodeSnippet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeRepository extends CrudRepository<CodeSnippet, String> {
    
    List<CodeSnippet> findAllByTimeRestrictedAndViewRestrictedOrderByDateDesc(boolean timeRestricted
                                                                            , boolean viewRestricted);
    
    @Query("SELECT snippet FROM CodeSnippet snippet " +
            "WHERE snippet.time IS NULL AND snippet.views IS NULL " +
            "ORDER BY snippet.date DESC")
    List<CodeSnippet> findLastSnippets(Pageable pageable);
    
}
