### Download the dataset
* Download the ["Amazon fine food reviews"](https://www.kaggle.com/snap/amazon-fine-food-reviews/downloads/amazon-fine-food-reviews.zip/2) dataset
* Extract a file "Reviews.csv" into a folder called "amazon-fine-food-reviews" so that all reviews are in this path 
```
amazon-fine-food-reviews/Reviews.csv
``` 

### Run this example in this folder
```
../gradlew build
java -cp build/libs/basic_word_count-1.0-SNAPSHOT.jar edu.rit.cs.basic_word_count.WordCount_Seq
```
