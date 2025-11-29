package ec.edu.istr.violentometro.service;

import java.util.List;
import java.util.Optional;

public interface BaseService <E>{
    E save(E entity) throws Exception;
    List<E> findAll() throws Exception;
    Optional<E> findById(Integer id) throws  Exception;
    E updateOne(E entity, Integer id) throws Exception;
    boolean deleteById(Integer id) throws Exception;
}
