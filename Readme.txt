
FoodAnalyzer:
This is an Android Project that is created to retrieve the list of food items from the website http://ndb.nal.usda.gov/ndb/api/doc
and display their nutrients details to the user.

Flow for the application:
- Launch the FoodAnalyzer application by selecting the icon on an Android device.
- Splash screen is displayed while the application does a HTTP GET to retrieve the list of Food items. Currently max of 
items retrieved are 15 .
- The UI will display the list of good items.
- User is expected to select an item from the list.
- On clicking the button "Get Details" the application displays the Protien content for that item.
 

Note: Tested this application on Motorola G running Android version 5.0.2
The project is created using Android  Studio IDE.
Basic feature testing is performed.
Application performs HTTP GET to retreive the JSON from the website.
Application parses the JSON to get the Food Items and their details.
Applicaiton is using AsycTask while connecting to the internet.
Source code does not include the key provided by the website. Please obtain a key and copy it in the file FoodItem.java 
Application crashes when the key is not provided or when the button "Get Details" is selected immediately after selecting the item. Few secs gap is expected for normal operation.

Pending items: 
Adding error handling code.
Handling thread sync
Adding complete set of features.
Fancy UI etc
