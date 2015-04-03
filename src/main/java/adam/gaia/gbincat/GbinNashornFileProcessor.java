package adam.gaia.gbincat;

import adam.gaia.gbin.GOGDescriptor;
import gaia.cu9.archivearchitecture.core.dm.CatalogueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Traite les objets du gbin avec un script Javascript.
 */
public class GbinNashornFileProcessor extends GbinFileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(GbinNashornFileProcessor.class);

    private String script = "" +
            "print(\"objet : \" + data.getAlpha() + \" \" + data.getDelta());" +
            "buffer.setAttribute(0, data.getAlpha());" +
            "buffer.setAttribute(1, data.getDelta());";
    private ScriptEngine engine;

    public GbinNashornFileProcessor(Configuration config, OutputTuple outputTuple) {
        super(config, outputTuple);
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn");
    }

    @Override
    protected Class getSourceClass() {
        return GOGDescriptor.getSourceClass();
    }

    @Override
    protected void extractGbinObject(Object o, OutputTuple outputTuple) throws Exception {
        CatalogueSource gogData = (CatalogueSource)o;
        logger.trace("Traitement de {}, {}", gogData.getAlpha(), gogData.getDelta());
        engine.put("data", o);
        engine.put("buffer", outputTuple);
        engine.eval(script);
    }
}
