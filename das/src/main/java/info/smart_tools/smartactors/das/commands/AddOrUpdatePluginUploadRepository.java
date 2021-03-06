package info.smart_tools.smartactors.das.commands;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.das.utilities.CommandLineArgsResolver;
import info.smart_tools.smartactors.das.utilities.ProjectResolver;
import info.smart_tools.smartactors.das.models.Feature;
import info.smart_tools.smartactors.das.models.Plugin;
import info.smart_tools.smartactors.das.models.Project;
import info.smart_tools.smartactors.das.models.UploadRepository;
import info.smart_tools.smartactors.das.utilities.exception.InvalidCommandLineArgumentException;
import info.smart_tools.smartactors.das.utilities.exception.ProjectResolutionException;
import info.smart_tools.smartactors.das.utilities.interfaces.ICommandLineArgsResolver;
import info.smart_tools.smartactors.das.utilities.interfaces.IProjectResolver;

import java.util.ArrayList;
import java.util.List;

public class AddOrUpdatePluginUploadRepository implements IAction {

    @Override
    public void execute(Object o)
            throws ActionExecuteException, InvalidArgumentException {
        System.out.println("Adding/updating repository to the plugin ...");

        try {
            ICommandLineArgsResolver clar = (ICommandLineArgsResolver) ((Object[])o)[0];
            IProjectResolver pr = (IProjectResolver) ((Object[])o)[1];
            Project project = pr.resolveProject();
            Feature feature = null;
            String featureName = clar.getFeatureName();
            List<Feature> features = new ArrayList<>();
            if (null != featureName) {
                feature = project.getFeatures().stream().filter(f -> f.getName().equals(featureName)).findFirst().orElse(null);
            }
            if (null == feature) {
                feature = pr.getCurrentFeature();
            }
            if (null != feature) {
                features.add(feature);
            }
            if (null == featureName || featureName.toLowerCase().equals("all")) {
                features = project.getFeatures();
            }
            String pluginName = clar.getPluginName();


            String repId = clar.getUploadRepositoryId();
            String repUrl = clar.getUploadRepositoryUrl();

            for(Feature f : features) {
                System.out.println("Feature - " + f.getName() + " ...");
                List<Plugin> plugins = new ArrayList<>();
                if (null == pluginName || pluginName.toLowerCase().equals("all")) {
                    plugins.addAll(f.getPlugins());
                } else {
                    Plugin p = f.getPlugins().stream()
                            .filter(pl -> pl.getRawName().equals(pluginName)).findFirst().orElse(null);
                    if (null != p) {
                        plugins.add(p);
                    }
                }
                for (Plugin p : plugins) {
                    System.out.println("Plugin - " + p.getRawName() + " ...");
                    forOnePlugin(p, repId, repUrl);
                    System.out.println("... processed.");
                }
            }

            // Save project meta data file
            project.saveMetaDataFile();

        } catch (InvalidCommandLineArgumentException | ProjectResolutionException e) {
            System.out.println(e.getMessage());

            return;
        } catch (Exception e) {
            System.out.println("Addition/update repository has been failed.");
            System.err.println(e);

            throw new ActionExecuteException(e);
        }
        System.out.println("Repository has been added/updated successful.");
    }

    private void forOnePlugin(final Plugin plugin, final String repId, final String repUrl)
            throws Exception {
        // Addition or update plugin repository to pom file
        plugin.addOrUpdateUploadRepositoryToPom(repId, repUrl);

        // Addition or update plugin repository to meta data
        plugin.addOrUpdateUploadRepository(new UploadRepository(repId, repUrl));

    }
}
