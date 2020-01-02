package com.pueblolavanda.singlecards;

import com.pueblolavanda.singlecards.cases.CSVLoaderResponseModel;
import com.pueblolavanda.singlecards.cases.ExpansionResponseModel;
import com.pueblolavanda.singlecards.cases.FetchCSVLoaderInteractor;
import com.pueblolavanda.singlecards.cases.FetchExpansionInteractor;
import com.pueblolavanda.singlecards.drivers.device.Writer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SinglecardsApplication implements CommandLineRunner {

	FetchExpansionInteractor interactor;
	FetchCSVLoaderInteractor csvLoaderInteractor;

	@Autowired
	public SinglecardsApplication(FetchExpansionInteractor interactor, FetchCSVLoaderInteractor csvLoaderInteractor) {
		this.interactor = interactor;
		this.csvLoaderInteractor = csvLoaderInteractor;
	}

	public static void main(String[] args) {
		SpringApplication.run(SinglecardsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		ExpansionResponseModel expansionModel = interactor.fetch(args[0], args[3]);
		expansionModel = interactor.removeCard(expansionModel.getExpansion());
		expansionModel = interactor.setAsNotFoil(expansionModel.getExpansion());
		expansionModel = interactor.fetchCardSitePrice(expansionModel.getExpansion());


		final CSVLoaderResponseModel csvLoaderModel = csvLoaderInteractor.fetchNewCardProducts(expansionModel);
		final String filename = args[1];
		final String path = args[2];

		Writer writer = new Writer();
		writer.csvWriter(csvLoaderModel,filename);
		writer.downloadImage(path,expansionModel);

	}


}
