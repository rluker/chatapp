import java.time.*;

public class TestMe {

    /**
     * Trim asterisks from specific field.
     */
    public String trimAsterisks( String field ) {
        System.out.println("Removing * from " + field + "....");
        field = field.replaceAll( "[*]", "" );
        System.out.println(field);
        return field;
    }
    public static void main(String[] args) {

        System.out.println( Instant.now() );

        TestMe test = new TestMe();

        String aghAsteriks = "***REMOVE THEM***";
        aghAsteriks = test.trimAsterisks(aghAsteriks);
        System.out.println(aghAsteriks);
    }
}