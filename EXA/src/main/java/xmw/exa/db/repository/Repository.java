package xmw.exa.db.repository;

import java.util.List;

public interface Repository<T> {
    List<T> all();

    T get(long id);

    boolean create(T data);

    T update(T data);

    T delete(long id);
}