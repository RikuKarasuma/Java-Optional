package xyz.softwareeureka.optional;

import static java.util.Optional.*;
import static java.lang.System.out;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.function.Supplier;

public class OptionalPractice
{



    public static void main(String[] args)
    {
        // To define a empty option object we must
        // call a static method inside Optional called
        // empty.
        // Which will create our Empty optional object
        // for us.

        // Empty Optional object 
        Optional<String> emptyOptional = empty();

        // Test if our value is present.
        // As we are expecting it not to be.
        // It should output that our empty optional is 
        // empty.
        if(!emptyOptional.isPresent())
            out.println("Our string is empty...");


        // To create an Optional instance of some 
        // value type we can use Optional.of function.
        // This function can't handle a null and will
        // throw a null pointer exception.
        
        // Test value to pass through to of function.
        String testValue = "Optional practice";

        // Optional created from of function with passed 
        // test value String.
        Optional<String> stringOptional = of(testValue);

        // Check that our Optional value is present.
        // As we are expecting it to be present.
        // It should print out the value.
        if(stringOptional.isPresent())
            out.println("Optional value from of: " + stringOptional.get());


        // To create an Optional instance of some value
        // type from which we might be expecting a null
        // value, we need to use ofNullable. This will
        // not throw a null pointer exception.

        // Create an Optional instance with null value.
        Optional<String> nullOptional = ofNullable(null);

        // We can check whether an Optional contains a null
        // by using the isEmpty function. 
        if(nullOptional.isEmpty())
            out.println("Our null Optional is null.");

        // Using Optional we can pass a literal function
        // using ifPresent, which will enable us to act
        // on a value if there's a nonNull within.

        Optional<String> lambdaOptional = of(testValue);

        // Writing the if present implicitly...
        lambdaOptional.ifPresent(out::println);

        // Is the same as explicitly,
        lambdaOptional.ifPresent( str -> out.println(str));

        // In case we are expecting a value and it doesn't
        // exist. We can use orElse function to return a 
        // value that is defaultly used.

        // We need to created a typed null value, or 
        // else the orElse function will not work properly,
        // as it has no class information from the desired
        // type.
        String nullValueType = null;

        // Create our Optional from a default value using
        // our null that we passed.
        // Important to note that orElse will always execute.
        // Even if the value is present. 
        // So this presents significant overhead, if the call
        // is to a REST service or Database and should be considered
        // in each design.
        String orElseOptionalValue = ofNullable(nullValueType).orElse("Default value string");

        // Print out our default value that we expect because
        // of our passed null.
        out.println(orElseOptionalValue);

        // Create our Optional from a default literal function
        // using orElseGet. Which will allow us to execute lambdas
        // if our expected value doesn't exist.
        // Important to note that orElseGet doesn't execute if the 
        // value is present. Which cuts out overhead compared to
        // orElse function.
        String orElseGetOptionalValueFromFunction = ofNullable(nullValueType).orElseGet( () -> "Default value from literal function.");

        // Our literal function created our default value once it 
        // recognized a null within our Optional. 
        out.println(orElseGetOptionalValueFromFunction);

        // We can throw a custom exception using the function 
        // orElseThrow. This will allow us to handle certain
        // error situations that arise from a value not being
        // present.
        try
        {
            ofNullable(nullValueType).orElseThrow(NullPointerException::new);
        }
        catch(NullPointerException expectedValueError)
        {
            out.println("Caught our expected value error.");
        }

        // There is a filter function which allows us to select certain 
        // values.
        Optional<String> filteredOptional = of(testValue).filter( str -> str.contains("practice"));

        // Print the value we selected with filter.
        out.println(filteredOptional.get());

        // We can also use the filter function to check for certain value 
        // conditions.
        if(!filteredOptional.filter(str -> str.contains("boondogal")).isPresent())
            out.println("Successfully verified value...");

        
        // Using the map function. We can transform the wrapped value inside
        // our Optional instance.
        Optional<String> transformedOptionalString = of(testValue);

        // Expected string length to validate.
        final int expectedStringLength = 17;
        // Transform our String into its character length.
        transformedOptionalString.map(String::length)
            // Select our value.
            .filter(length -> length == expectedStringLength)
            // Check that or transformation worked correctly and retrieved
            // the expected character length.
            .ifPresent( length -> out.println("String length validated"));


        // Using flatMap, we can access nested wrapped values. Such
        // as Optional<Optional<String>>.

        // Create an Optional with a nested String Optional inside.
        Optional<Optional<String>> nestedOptionals = of(of(testValue));

        // Extract our String inside the nested Optional using flatMap 
        // with orElse.
        String extractedNestedString = nestedOptionals.flatMap(nestedOptional -> nestedOptional)
            .orElse("");

        // Print out our extracted String from the nested Optional.
        out.println(extractedNestedString);

        // If we need to assess a group of Optionals. We can use 
        // Stream to create a functional collection of them.
        // This will allow us to find the first non empty 
        // Optional or transform each Optional into a desired
        // value.

        // Keep in mind that chaining Optionals in this manner
        // causes each get method to be executed regardless of
        // whether or not the predicate was already complete.

        // Create our list of names to be inserted into a stream.
        Optional<String> nameOne = of(""), nameTwo = of("John"), nameThree = of("Steve"),
            nameFour = ofNullable(null);

        // Create our stream of names.
        Stream<Optional<String>> names = Stream.of(nameOne, nameTwo, nameThree, nameFour);

        // We can now use the Streams functional methods to filter
        // out and select only the value we need.
        String firstNonEmptyName = names
            // Only get our non null values.
            .filter(Optional::isPresent)
            // Transform them into strings.
            .map(Optional::get)
            // Retrieve only the non empty names.
            .filter( name -> !name.isEmpty())
            // Find the first one.
            .findFirst().get();

        // Print out the first non empty name our 
        // Stream has found.
        out.println(firstNonEmptyName);

        // In order to do the above lazily, we must use the 
        // Supplier interface.
        // We will reuse the names above. With a new Stream Type.
        
        // Create a Stream of Optionals nested inside a Supplier interface.
        Stream<Supplier<Optional<String>>> lazilyEvaluatedStream = Stream.of(
            () -> nameFour, () -> nameOne, () -> nameThree, () -> nameTwo);

        // Using the Supplier interface and using lambdas/functions for value
        // insertion, we can lazily evaluate our Stream variables. Making our 
        // filters and maps more efficient.
        firstNonEmptyName = lazilyEvaluatedStream.map(Supplier::get)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(name -> !name.isEmpty())
            .findFirst()
            .get();

        // Print out name that was found not empty or null.
        out.println(firstNonEmptyName);
    }
}