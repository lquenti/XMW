package xmw.exa.models.modules;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import xmw.exa.db.DB;
import xmw.exa.db.ExaElement;
import xmw.exa.db.repository.BaseXmlRepository;
import xmw.flush.Module;
import xmw.flush.Modules;

import java.util.List;

public class ModuleRepository extends BaseXmlRepository<Module> {

    public ModuleRepository(Context context) {
        super(context, Modules.class, Module.class);
    }

    @Override
    public List<Module> all() {
        try {
            var root = DB.getRootChildMap(context);
            Modules modules = (Modules) root.get(ExaElement.MODULES);
            return modules.getModule();
        } catch (BaseXException e) {
            throw new RuntimeException("Failed to query modules: " + e.getMessage(), e);
        }
    }
}
