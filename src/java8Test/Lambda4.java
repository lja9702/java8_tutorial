package java8Test;

class Lambda4 {
    static int outerStaticNum;	//static variable
    int outerNum;				//instance field

    void testScopes() {
        //5-2. fields and static variables
        Converter<Integer, String> stringConverter2 = (from) -> {
            outerNum = 52;
            return String.valueOf(outerNum);
        };

        System.out.println(stringConverter2.convert(23));

        Converter<Integer, String> stringConverter3 = (from) -> {
            outerStaticNum = 32;
            return String.valueOf(outerStaticNum);
        };

        System.out.println(stringConverter3.convert(33));
    }
}