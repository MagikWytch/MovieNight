package io.magikwytch.movienights.repository;

import io.magikwytch.movienights.domain.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, String> {

    //Movie findDistinctFirstByTitleIgnoreCase(String Title);
    Movie findByImdbId(String id);

}
