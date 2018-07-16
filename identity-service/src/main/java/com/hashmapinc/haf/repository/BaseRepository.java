package com.hashmapinc.haf.repository;

import com.hashmapinc.haf.page.PaginatedRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T, I extends Serializable> extends JpaRepository<T, I> {

    List<T> findPaginated(PaginatedRequest request, PageRequest page);
}
