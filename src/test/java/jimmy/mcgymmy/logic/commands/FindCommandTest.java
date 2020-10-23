package jimmy.mcgymmy.logic.commands;

import static jimmy.mcgymmy.testutil.TypicalFoods.CHICKEN_RICE;
import static jimmy.mcgymmy.testutil.TypicalFoods.CRISPY_FRIED_FISH;
import static jimmy.mcgymmy.testutil.TypicalFoods.DANISH_COOKIES;
import static jimmy.mcgymmy.testutil.TypicalFoods.NASI_LEMAK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import jimmy.mcgymmy.commons.core.Messages;
import jimmy.mcgymmy.logic.parser.CommandParserTestUtil;
import jimmy.mcgymmy.logic.parser.exceptions.ParseException;
import jimmy.mcgymmy.logic.predicate.DatePredicate;
import jimmy.mcgymmy.logic.predicate.FoodContainsKeywordsPredicate;
import jimmy.mcgymmy.logic.predicate.NameContainsKeywordsPredicate;
import jimmy.mcgymmy.logic.predicate.TagContainsKeywordsPredicate;
import jimmy.mcgymmy.model.Model;
import jimmy.mcgymmy.model.ModelManager;
import jimmy.mcgymmy.model.UserPrefs;
import jimmy.mcgymmy.model.food.Food;
import jimmy.mcgymmy.testutil.TypicalFoods;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(TypicalFoods.getTypicalMcGymmy(), new UserPrefs());
    private Model expectedModel = new ModelManager(TypicalFoods.getTypicalMcGymmy(), new UserPrefs());

    @Test
    public void execute_zeroKeywords_noFoodFound() {
        String expectedMessage = String.format(Messages.MESSAGE_FOOD_LISTED_OVERVIEW, 0);
        FoodContainsKeywordsPredicate predicate = preparePredicate(" ");
        FindCommand command = new FindCommand();
        command.setParameters(
                new CommandParserTestUtil.OptionalParameterStub<>("", predicate),
                new CommandParserTestUtil.OptionalParameterStub<>("n"),
                new CommandParserTestUtil.OptionalParameterStub<>("t"),
                new CommandParserTestUtil.OptionalParameterStub<>("d"));
        expectedModel.updateFilteredFoodList(predicate);
        ObservableList<Food> curr = expectedModel.getFilteredFoodList();
        CommandTestUtil.assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredFoodList());
    }

    @Test
    public void execute_multipleKeywords_multipleFoodsFound() {
        String expectedMessage = String.format(Messages.MESSAGE_FOOD_LISTED_OVERVIEW, 2);
        FoodContainsKeywordsPredicate predicate = preparePredicate("CHICKEN fish");
        FindCommand command = new FindCommand();
        command.setParameters(
                new CommandParserTestUtil.OptionalParameterStub<>("", predicate),
                new CommandParserTestUtil.OptionalParameterStub<>("n"),
                new CommandParserTestUtil.OptionalParameterStub<>("t"),
                new CommandParserTestUtil.OptionalParameterStub<>("d"));
        expectedModel.updateFilteredFoodList(predicate);
        CommandTestUtil.assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CHICKEN_RICE, CRISPY_FRIED_FISH),
                model.getFilteredFoodList());
    }

    @Test
    public void execute_validDate_singleFoodFound() throws ParseException {
        String expectedMessage = String.format(Messages.MESSAGE_FOOD_LISTED_OVERVIEW, 1);
        DatePredicate datePredicate = new DatePredicate("20-04-2020");
        FindCommand command = new FindCommand();
        command.setParameters(
                new CommandParserTestUtil.OptionalParameterStub<>(""),
                new CommandParserTestUtil.OptionalParameterStub<>("n"),
                new CommandParserTestUtil.OptionalParameterStub<>("t"),
                new CommandParserTestUtil.OptionalParameterStub<>("d", datePredicate));
        expectedModel.updateFilteredFoodList(datePredicate);
        CommandTestUtil.assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CHICKEN_RICE), model.getFilteredFoodList());
    }

    @Test
    public void execute_validNameDateButInvalidTag_noFoodFound() throws ParseException {
        String expectedMessage = String.format(Messages.MESSAGE_FOOD_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate namePredicate = prepareNamePredicate("chicken");
        TagContainsKeywordsPredicate tagPredicate = prepareTagPredicate("dinner");
        DatePredicate datePredicate = new DatePredicate("20-04-2020");
        FindCommand command = new FindCommand();
        command.setParameters(
                new CommandParserTestUtil.OptionalParameterStub<>(""),
                new CommandParserTestUtil.OptionalParameterStub<>("n", namePredicate),
                new CommandParserTestUtil.OptionalParameterStub<>("t", tagPredicate),
                new CommandParserTestUtil.OptionalParameterStub<>("d", datePredicate));
        Predicate<Food> combinedPredicate = namePredicate.and(tagPredicate).and(datePredicate);
        expectedModel.updateFilteredFoodList(combinedPredicate);
        CommandTestUtil.assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredFoodList());
    }

    /**
     * Parses {@code userInput} into a {@code FoodContainsKeywordsPredicate}.
     */
    private FoodContainsKeywordsPredicate preparePredicate(String userInput) {
        return new FoodContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }

    /**
     * Parses {@code userInput} into a {@code TagContainsKeywordsPredicate}.
     */
    private TagContainsKeywordsPredicate prepareTagPredicate(String userInput) {
        return new TagContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate prepareNamePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }
    @Test
    public void execute_validTag_multipleFoodsFound() {
        String expectedMessage = String.format(Messages.MESSAGE_FOOD_LISTED_OVERVIEW, 3);
        TagContainsKeywordsPredicate tagPredicate = prepareTagPredicate("lunch");
        FindCommand command = new FindCommand();
        command.setParameters(
                new CommandParserTestUtil.OptionalParameterStub<>(""),
                new CommandParserTestUtil.OptionalParameterStub<>("n"),
                new CommandParserTestUtil.OptionalParameterStub<>("t", tagPredicate),
                new CommandParserTestUtil.OptionalParameterStub<>("d"));
        expectedModel.updateFilteredFoodList(tagPredicate);

        CommandResult commandResult = command.execute(model);
        assertEquals(model.getFilteredFoodList(), expectedModel.getFilteredFoodList());

        assertEquals(commandResult.getFeedbackToUser(), expectedMessage);
        assertEquals(new CommandResult(expectedMessage), commandResult);
        assertEquals(expectedModel, model);

        //CommandTestUtil.assertCommandSuccess(command, model, expectedMessage, expectedModel);
        ArrayList<Food> arrayList = new ArrayList<>(model.getFilteredFoodList());
        assertEquals(Arrays.asList(CHICKEN_RICE, NASI_LEMAK, DANISH_COOKIES), arrayList);
    }
}
