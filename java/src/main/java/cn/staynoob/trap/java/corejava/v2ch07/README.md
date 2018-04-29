# 7 Internationalization
## 1 Locales
To control the formatting, use the Locale class. A locale is made up of up to five components:
1. A language, specified by two or three lowercase letters, such as en, de, or zh.
2. Optionally, a script, specified by for letters with an initial uppercase, such as Latn(Latin), Cyrl(Cyrillic), or Hant(traditional Chinese characters). This can be useful because some languages, such as Serbian, are written in Latin or Cyrillic, and some Chinese readers prefer the traditional over the simplified characters.
3. Optionally, a country or region, specified by two uppercase letters or three digits, such as US or CH(Switzerland).
4. Optionally, a variant, specifying miscellaneous features such as dialects or spelling rules. Variants are rarely used nowadays. There used to be a "Nynorsk" variant of Norwegian, but it is now expressed with a different language code, nn. What used to be variants for the Japanese imperial calendar and Thai numerals are now expressed as extensions.
5. Optionally, an extension. Extensions describe local preferences for calendars(such as the Japanese calendar), numbers(Thai instead of Western digits), and so on. The Unicode standard specified some of these extensions. Extensions start with u- and a two-letter code specifying whether the extension deals with the calendar(ca), numbers(nu), and so on. For example, the extension u-nu-thai denotes the use of Thai numerals. Other extensions are entirely arbitrary and start with x=, such as x-java.
Locales are described by tags=hyphenated strings of locale elements such as en-US.\
In Germany, you would use a locale de-DE. Switzerland has four official languages(German, French, Italian, and Rhaeto-Romance). A German speaker in Switzerland would want to use a locale de-CH. This locale uses the rules for the German language, but currency values are expressed in Swiss francs, not euros. If you only specify the language, say, de, then the locale cannot be used for country-specific issues such as currencies.\
You can construct a Locale object from a tag string like this:
```java
Locale usEnglish = Locale.forLanguageTag("en-US");
```
The toLanguageTag method yields the language tag for a given locale. For example, Locale.US.toLanguageTag() is the string "en-US". Finally, the static getAvailableLocales method returns an array of all locales known to the virtual machine.\
Besides constructing a locale or using a predefined one, you have two other methods for obtaining a locale object. The static getDefault method of the Locale class initially gets the default locale as stored by the local operating system. You can change the default Java locale by calling setDefault; however, that change only affects your program, not the operating system. All locale-dependent utility classes can return an array of the locales they support.
Once you have a locale, what can you do with it? Not much, as it turns out. The only useful methods in the Locale class are those for identifying the language and country/region codes. The most important one is getDisplayName. It returns a string describing the locale.

## 2 Number Formats
The Java library supplies a collection of formatter objects that can format and parse numeric values in the java.text package. Go through the following steps to format a number for a particular local:
1. Get the locale object, as described in the preceding section.
2. Use a "factory method" to obtain a formatter object.
3. Use the formatter object for formatting and parsing.
The factory methods are static methods of the NumberFormat class that take a Locale argument. There are three factory methods: getNumberInstance, getCurrencyInstance, and getPercentInstance. These methods return objects that can format and parse numbers, currency amounts, and percentages, respectively. For example, here is how you can format a currency value in German:
```java
Locale loc = Locale.GERMAN;
Number format currFmt = NumberFormat.getCurrencyInstance(loc);
double amt = 123456.78;
String result = currFmt.format(amt);
```
Conversely, to read in a number that was entered or stored with the conventions of a certain locale, use the parse method.

## 3 Currencies
To format a currency value, you can use the NumberFormat.getCurrencyInstance method. However, that method is not very flexible--it returns a formatter for a single currency. Suppose you prepare an invoice for an American customer in which some amounts are in dollars and others are in euros. You can't just use two formatters. You invoice would look very strange. Instead, use the Currency class to control the currency used by the formatters. You can get a Currency object by passing a currency identifier to the static Currency.getInstance method. Then call the setCurrency method for each formatter. Here is how you would set up the euro formatter for you American customer:
```java
NumberFormat euroFormatter = NumberFormat.getCurrencyInstance(Locale.US);
euroFormatter.setCurrency(Currency.getInstance("EUR"));
```

## 4 Date and Time
When you are formatting date and time, you should be concerned with for local-dependent issues:
- The names of months and weekdays should be presented in the local language.
- There will be local preferences for the order of year, month, and day.
- The Gregorian calendar might not be the local preference for expressing date.
- The time zone of the location must be taken into account.
The DateTimeFormatter class from the java.time package handles these issues.
```java
FormatStyle style = FormatStyle.SHORT;
DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(style);
DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(style);
DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(style);
// or DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(style1, style2);
```
These formatters use the current locale. To use a different locale, use the withLocale method. Now you can format a LocalDate, LocalTime, LocalDateTime or ZonedDateTime.
> Note: Here we use the DateTimeFormatter class from the java.time package. There is also a legacy java.text.DateFormatter class from Java 1.1 that works with Date and Calendar objects.

You can use one of the static parse methods of LocalDate, LocalDateTime, LocalTime, or ZonedDateTime to parse a date or time in a string.

## 5 Collation and Normalization
Most programmers know how to compare string with the compareTo method of the String class. Unfortunately, when interacting with human users, this method is not very useful. The compareTo method uses the values of the UTF-16 encoding of the string, which leads to absurd results, even in English. To obtain a locale-sensitive comparator, call the static Collator.getInstance method:
```
Collator coll = Collator.getInstance(locale);
words.sort(coll); // Collator implements Comparotr<Object>
```
Since the Collator class implements the Comparator interface, you can pass a Collator object to the List.sort(Comparator) method to sort a list of strings.

## 6 Message Formatting
### Formatting Numbers and Dates
skipped
### Choice Formats
skipped

## 7 Text Input and Output
As you know, the Java programming language itself is fully Unicode-based. However, Windows and Mac OS X still support legacy character encodings such as Windows-1252 or Mac Roman in Western European countries. Therefore, communicating with your users through text is not as simple as it should be. The following sections discuss the complications that you may encounter.
### Text Files
Nowadays, it is best to use UTF-8 for saving and loading text files. But you may need to work with legacy files. If you know the expected character encoding, you can specify it when writing or reading text files:
```java
PrintWriter out = new PrintWriter(filename, "Windows-1252");
```
For a guess of the best encoding to use, get the "platform encoding" by calling
```java
Charset platforEncoding = Charset.defaultCharset();
```
### Line Endings
skipped

### The Console
skipped

### Log Files
skipped

### The UTF-8 Byte Order Mark
skipped(see ch02)

### Character Encoding of Source Files
When a program is compiling and running, three character encodings are involved:
- Source files: platform encoding
- Class files: modified UTF-8
- Virtual machine: UTF-16
You can specify the character encoding of your source files with the -encoding flag, for example:
```bash
javac -encoding UTF-8 Myfile.java
```
## 8 Resource Bundles
### Locating Resource Bundles
When localizing an application, you produce a set of resource bundles. Each bundle is a property file or a class that describes locale-specific items(such as messages labels, and so on). For each bundle, you have to provide versions for all locals that you want to support.\
You need to use a specific naming convention for these bundles. For example, resources specific to Germany go into a file bundleName\_de\_DE, whereas those shared by all German-speaking countries go into bundleName\_de. In general, use
```
bundleName_language_country
```
for all country-specific resources, and use
```
bundleName_language
```
for all language-specific resources. Finally, as a fallback, you can put defaults into a file without any suffix.
```
bundleName
```
To load a bundle, use the command
```java
ResourceBundle bundle = ResourceBundle.getBundle(bundleName, currenctLocale);
```
The getBundle method attempts to load the bundle that matches the current locale by language and country/region. If it is not successful, the country/region and the language are dropped in turn. Then the same search is applied to the default locale, and finally, the default bundle file is consulted. If even that attempt fails, the method throws a MissingResourceException. That is, the getBundle method tries to load the following bundles:
```
bundleName_currentLocaleLanguage_currentLocaleCountry
bundleName_currentLocaleLanguage
bundleName_currentLocaleLanguage_defaultLocaleCountry
bundleName_defaultLocaleLanguage
bundleName
```
Once the getBundle method has located a bundle(say, bundleName\_de\_DE), it will still keep looking for bundleName\_de and bundleName. If these bundles exist, they become the parents of the bundleName\_de\_DE bundle in a resource hierarchy. Later, when looking up a resource, the parents are searched if a lookup was not successful in the current bundle. That is, if a particular resource was not found in bundleName\_de\_DE, then the bundleName\_de and bundleName will be queries as well.

> NOTE: We simplified the discussion of resource bundle lookup. If a locale has a script or variant, the lookup is quire a bit more complex. See the documentation of the method `Resource.Control.getCandidateLocales` for the gory details.

> TIP: You need not place all resources for your application into a single bundle. You could have one bundle for button labels, one for error messages, and so on.

### Property Files
skipped

> CAUTION: Files for storing properties are always ASCII files. If you need to place a Unicode character into a property file, encode it using the \uxxxx encoding. You can use the native2ascii tool to generate these files.

### Bundle Classes
To provide resources that are not strings, define classes that extend the ResourceBundle class. Use the standard naming convention to name your classes.

> CAUTION: When searching for bundles, a bundle in a class is given preference over a property file when the two bundles have the same base names.

Each resource bundle class implements a lookup table. You need to provide a key string for each setting you want to localize, and use that key string to retrieve the setting. For example,
```java
Color backgroundColor = (Color) bundle.getObject("backgroundColor");
doube[] paperSize = (double[]) bundle.getObject("defaultPaperSize");
```
The simplest way to implement resource bundle classes is to extend the ListResourceBundle class. The ListResourceBundle lets you place all your resources into an object array and then does the lookup for you. Alternatively, your resource bundle classes can extend the ResourceBundle class. Then you need to implement two methods, to enumerate all keys and to look up the value for a given key. The getObject method of the ResourceBundle class calls the handleGetObject method that you supply.



