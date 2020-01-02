package com.pueblolavanda.singlecards.drivers.device;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.pueblolavanda.singlecards.cases.CSVLoaderResponseModel;
import com.pueblolavanda.singlecards.cases.ExpansionResponseModel;
import com.pueblolavanda.singlecards.cases.ProductResponseModel;
import com.pueblolavanda.singlecards.domain.Card;
import com.pueblolavanda.singlecards.domain.Expansion;
import com.pueblolavanda.singlecards.domain.Product;

import java.io.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static com.pueblolavanda.singlecards.StringHelper.cleanFilename;

public class Writer {

    public void csvWriter(CSVLoaderResponseModel csvLoaderResponseModel, String filename) {


        List<Product> products = csvLoaderResponseModel.getProducts();

        CsvMapper mapper = new CsvMapper();
        mapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        CsvSchema schema = mapper.schemaFor(Product.class)
                .withColumnSeparator('\t')
                .withColumnReordering(true)
                .withHeader();
        fileWriter(filename, products, mapper, schema);

    }

    public void errorProductWriter(List<ProductResponseModel> productsHasError, String filename) {

        List<Product> products = new ArrayList<>();

        for(ProductResponseModel productResponseModel : productsHasError){
             products.add(productResponseModel.getProduct());
        }


        CsvMapper mapper = new CsvMapper();
        mapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        CsvSchema schema = mapper.schemaFor(Product.class)
                .withColumnSeparator(',')
                .withColumnReordering(true)
                .withHeader();

        fileWriter(filename, products, mapper, schema);

    }


    public void downloadImage(String path, ExpansionResponseModel expansionModel) throws IOException {
        Expansion expansion = expansionModel.getExpansion();
        for (Card card : expansion.getCards()) {
            if(card.getLayout().contains("normal") || card.getLayout().contains("split") ||
                    card.getLayout().contains("leveler") || card.getLayout().contains("flip")) {
                String imageURL = card.getImageUrl();

                imageURL = imageURL.replace("/es/","/en/");
                String filename = expansion.getName() + "_" + card.getCollectorNumber() + "_" + card.getName() + ".jpg";
                filename = cleanFilename(filename);
                try (InputStream in = new URL(imageURL).openStream()) {
                    Files.copy(in, Paths.get(path + filename), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            else
            {
                for (int i = 0; i < card.getFaces().size(); i++ ) {

                        String imageURL = card.getFaces().get(i).getImageUrl();
                        imageURL = imageURL.replace("/es/", "/en/");
                        String filename = expansion.getName() + "_" + card.getCollectorNumber() + "_" + card.getFaces().get(i).getName() + ".jpg";
                        filename = cleanFilename(filename);

                    try (InputStream in = new URL(imageURL).openStream()) {
                        Files.copy(in, Paths.get(path + filename), StandardCopyOption.REPLACE_EXISTING);
                    }
                }

            }
        }
    }


    private void fileWriter(String filename, List<Product> products, CsvMapper mapper, CsvSchema schema) {
        ObjectWriter myObjectWriter = mapper.writer(schema);
        File tempFile = new File(filename + ".csv");

        try {
            FileOutputStream tempFileOutputStream = new FileOutputStream(tempFile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(tempFileOutputStream,1024);
            OutputStreamWriter writerOutputStream = new OutputStreamWriter(bufferedOutputStream, "UTF-8");
            myObjectWriter.writeValue(writerOutputStream, products);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
