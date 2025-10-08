package functional_trims;


import functional_trims.datagen.TrimAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class FunctionalTrimsDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        // ✅ Correct: addProvider expects a RegistryDependentFactory or Factory functional interface
        pack.addProvider(TrimAdvancementProvider::new);
    }

}
