package ikd;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello Mahout world!
 * 
 */
public class BasicRecommender {

    private final static Logger LOGGER = LoggerFactory.getLogger(BasicRecommender.class);

    public static void main(String[] args) throws IOException, TasteException {
        final File dataFile = new File("/home/ubuntu/user_value.csv");
        final File outputFile = new File("/home/ubuntu/recommendations.csv");

        Set<Integer> charities = parseCharities(dataFile);

        // Load historical data about user preferences
        DataModel model = new FileDataModel(dataFile);

        // Compute the similarity between users, according to their preferences
        UserSimilarity similarity = new EuclideanDistanceSimilarity(model);

        // Group the users with similar preferences
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1,
                similarity, model);

        // Create a recommender
        UserBasedRecommender recommender = new GenericUserBasedRecommender(
                model, neighborhood, similarity);


        // For the user with the id 1 get two recommendations
        CSVWriter writer = new CSVWriter(new FileWriter(outputFile));

        for (Integer charity : charities) {
        List<RecommendedItem> recommendations = recommender.recommend(charity, 20);

            for (RecommendedItem recommendation : recommendations) {
                writer.writeNext(new String[]{
                        String.format("CH%d", charity),
                        String.valueOf(recommendation.getItemID()),
                        String.valueOf(recommendation.getValue())});
            }

        }
    }

    private static Set<Integer> parseCharities(File dataFile) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(dataFile));
        Set<Integer> sourceIds = new HashSet<>();

        String[] line;
        while((line = csvReader.readNext())!= null) {
            sourceIds.add(Integer.parseInt(line[0]));
        }
        return sourceIds;
    }
}
