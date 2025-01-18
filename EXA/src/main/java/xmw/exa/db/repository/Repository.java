package xmw.exa.db.repository;

import java.util.List;

public interface Repository<T> {
    List<T> all();

    T get(String id);

    boolean create(T data);

    T update(T data);

    boolean delete(String id);
}
