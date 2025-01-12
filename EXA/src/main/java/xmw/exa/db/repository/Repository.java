package xmw.exa.db.repository;

import java.util.List;

public interface Repository<T> {
    List<T> all();

    T getById(long id);
}