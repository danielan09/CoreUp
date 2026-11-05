package Repository;

import java.util.List;
import java.util.Optional;

public interface Repositorio<T> {
    void salvar(T item);
    Optional<T> buscarPorId(int id);
    List<T> listarTodos();
}
