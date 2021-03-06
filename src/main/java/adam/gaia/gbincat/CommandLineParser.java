package adam.gaia.gbincat;

import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Analyse la ligne de commande.
 */
public class CommandLineParser {
    public static final String INPUT_PATH_OPT = "d";
    public static final String NB_OBJECTS_OPT = "n";
    public static final String PROJECTION_OPT = "p";
    public static final String OUTFILE_OPT = "o";

    public static final String NB_OBJECTS_DEFAULT = "-1";
    public static final String PROJECTION_DEFAULT = "*";
    public static final String PROJECTION_SEP = ",";

    private final Options options = new Options();

    public CommandLineParser() {
        options.addOption(
                OptionBuilder.withArgName("inPath")
                        .hasArg()
                        .isRequired()
                        .withDescription("répertoire source des fichiers gbin")
                        .create(INPUT_PATH_OPT));
        options.addOption(
                OptionBuilder.withArgName("nbObjects")
                        .hasArg()
                        .withDescription("nombre d'objets à traiter")
                        .create(NB_OBJECTS_OPT));
        options.addOption(
                OptionBuilder.withArgName("projection")
                        .hasArg()
                        .withDescription("attributs à projeter")
                        .create(PROJECTION_OPT));
        options.addOption(
                OptionBuilder.withArgName("outFile")
                        .hasArg()
                        .isRequired()
                        .withDescription("fichier de sortie")
                        .create(OUTFILE_OPT));
    }

    public Configuration parse(String... args) throws ParseException {
        CommandLine commandLine = new PosixParser().parse(options, args, false);
        return new Configuration.ConfigurationBuilder()
                .inputPath(extractInputPathFrom(commandLine))
                .outputFile(extractOutputFilePathFrom(commandLine))
                .numberOfObjectsToProcess(extractNumberOfObjectsToProcessFrom(commandLine))
                .attributesToProject(extractAttributesToProjectFrom(commandLine))
                .build();
    }

    private Path extractInputPathFrom(CommandLine commandLine) {
        return Paths.get(commandLine.getOptionValue(INPUT_PATH_OPT));
    }

    private Path extractOutputFilePathFrom(CommandLine commandLine) {
        return Paths.get(commandLine.getOptionValue(OUTFILE_OPT));
    }

    /**
     * Analyse le nombre d'objets.
     * La chaîne représentant le nombre peut se terminer par K (ou k),
     * M (ou m), G (ou g) ou T (ou t) pour indiquer l'unité (K = x1000,
     * M = 1 000 000, G = 1 000 000 000, T = 1 000 000 000 000).
     */
    private long extractNumberOfObjectsToProcessFrom(CommandLine commandLine) {
        String toParse = commandLine.getOptionValue(NB_OBJECTS_OPT, NB_OBJECTS_DEFAULT);
        char last = toParse.toLowerCase().charAt(toParse.length() - 1);
        long multFactor = 1; // par défaut, à l'unité
        switch (last) {
            case 'k':
                multFactor = 1000;
                break;
            case 'm':
                multFactor = 1000000;
                break;
            case 'g':
                multFactor = 1000000000;
                break;
            case 't':
                multFactor = 1000000000000L;
                break;
        }
        if (multFactor > 1) {
            toParse = toParse.substring(0, toParse.length() - 1);
        }
        return Integer.parseInt(toParse) * multFactor;
    }

    private List<String> extractAttributesToProjectFrom(CommandLine commandLine) {
        return Arrays.asList(commandLine.getOptionValue(PROJECTION_OPT, PROJECTION_DEFAULT).split(PROJECTION_SEP));
    }

    /**
     * Affiche la syntaxe d'appel du programme.
     */
    public void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("gbinCat", options, true);
    }
}
