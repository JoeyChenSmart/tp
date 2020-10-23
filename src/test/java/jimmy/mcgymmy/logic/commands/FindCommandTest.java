package jimmy.mcgymmy.logic.commands;

import static jimmy.mcgymmy.testutil.TypicalFoods.CHICKEN_RICE;
import static jimmy.mcgymmy.testutil.TypicalFoods.CRISPY_FRIED_FISH;
import static jimmy.mcgymmy.testutil.TypicalFoods.DANISH_COOKIES;
import static jimmy.mcgymmy.testutil.TypicalFoods.NASI_LEMAK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import jimmy.mcgymmy.model.food.Name;
import jimmy.mcgymmy.testutil.FoodBuilder;
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

        String msg = commandResult.getFeedbackToUser();

        Food cr = new FoodBuilder().withName(new Name("Chicken Rice"))
                .withProtein("94351253").withFat("123")
                .withCarb("456").withDate("20 Apr 2020")
                .withTags("lunch").build();
        Food nl = new FoodBuilder().withName(new Name("Nasi Alamak"))
                .withProtein("98765432").withFat("321")
                .withCarb("123").withDate("2 Sep 2020")
                .withTags("dinner", "lunch").build();
        Food dc = new FoodBuilder().withName(new Name("Danish Cookies"))
                .withProtein("87652533").withFat("654")
                .withCarb("456").withDate("1 Jan 2020")
                .withTags("lunch").build();

        assertTrue(tagPredicate.test(cr));
        assertTrue(tagPredicate.test(nl));
        assertTrue(tagPredicate.test(dc));

        ArrayList<Food> arrayList = new ArrayList<>(model.getFilteredFoodList());
        assertEquals(Arrays.asList(CHICKEN_RICE, NASI_LEMAK, DANISH_COOKIES), arrayList);

        assertEquals(expectedMessage.charAt(1), msg.charAt(1));
        assertEquals(expectedMessage.charAt(2), msg.charAt(2));
        assertEquals(expectedMessage.charAt(3), msg.charAt(3));
        assertEquals(expectedMessage.charAt(4), msg.charAt(4));
        assertEquals(expectedMessage.charAt(5), msg.charAt(5));
        assertEquals(expectedMessage.charAt(6), msg.charAt(6));
        assertEquals(expectedMessage.charAt(7), msg.charAt(7));
        assertEquals(expectedMessage.charAt(8), msg.charAt(8));
        assertEquals(expectedMessage.charAt(9), msg.charAt(9));
        assertEquals(expectedMessage.charAt(10), msg.charAt(10));
        assertEquals(expectedMessage.charAt(11), msg.charAt(11));
        assertEquals(expectedMessage.charAt(12), msg.charAt(12));
        assertEquals(expectedMessage.charAt(13), msg.charAt(13));
        assertEquals(expectedMessage.charAt(14), msg.charAt(14));
        assertEquals(expectedMessage.charAt(15), msg.charAt(15));
        assertEquals(expectedMessage.charAt(16), msg.charAt(16));
        assertEquals(expectedMessage.charAt(17), msg.charAt(17));
        assertEquals(expectedMessage.charAt(18), msg.charAt(18));
        assertEquals(expectedMessage.charAt(19), msg.charAt(19));

        assertEquals(model.getFilteredFoodList().size(), expectedModel.getFilteredFoodList().size());

        assertNotEquals(model.getFilteredFoodList().size(), 0);
        assertNotEquals(model.getFilteredFoodList().size(), 1);
        assertNotEquals(model.getFilteredFoodList().size(), 2);
        //assertNotEquals(model.getFilteredFoodList().size(), 3);
        assertNotEquals(model.getFilteredFoodList().size(), 4);
        assertNotEquals(model.getFilteredFoodList().size(), 7);


        assertEquals(commandResult.getFeedbackToUser(), expectedMessage);
        assertEquals(new CommandResult(expectedMessage), commandResult);
        assertEquals(expectedModel, model);

        //CommandTestUtil.assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }
}
