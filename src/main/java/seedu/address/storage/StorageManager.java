package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.model.ReadOnlyMcGymmy;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;

/**
 * Manages storage of McGymmy data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private McGymmyStorage mcGymmyStorage;
    private UserPrefsStorage userPrefsStorage;

    /**
     * Creates a {@code StorageManager} with the given {@code McGymmyStorage} and {@code UserPrefStorage}.
     */
    public StorageManager(McGymmyStorage mcGymmyStorage, UserPrefsStorage userPrefsStorage) {
        super();
        this.mcGymmyStorage = mcGymmyStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ McGymmy methods ==============================

    @Override
    public Path getMcGymmyFilePath() {
        return mcGymmyStorage.getMcGymmyFilePath();
    }

    @Override
    public Optional<ReadOnlyMcGymmy> readMcGymmy() throws DataConversionException, IOException {
        return readMcGymmy(mcGymmyStorage.getMcGymmyFilePath());
    }

    @Override
    public Optional<ReadOnlyMcGymmy> readMcGymmy(Path filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return mcGymmyStorage.readMcGymmy(filePath);
    }

    @Override
    public void saveMcGymmy(ReadOnlyMcGymmy mcGymmy) throws IOException {
        saveMcGymmy(mcGymmy, mcGymmyStorage.getMcGymmyFilePath());
    }

    @Override
    public void saveMcGymmy(ReadOnlyMcGymmy mcGymmy, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        mcGymmyStorage.saveMcGymmy(mcGymmy, filePath);
    }

}
