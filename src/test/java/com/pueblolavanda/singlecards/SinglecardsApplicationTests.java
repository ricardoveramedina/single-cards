package com.pueblolavanda.singlecards;

import com.pueblolavanda.singlecards.cases.*;
import com.pueblolavanda.singlecards.client.ApiScryfall;
import com.pueblolavanda.singlecards.client.WebScraper;
import com.pueblolavanda.singlecards.controllers.UpdateDataController;
import com.pueblolavanda.singlecards.controllers.UpdatePricesController;
import com.pueblolavanda.singlecards.domain.Product;

import com.pueblolavanda.singlecards.drivers.db.ConnPL;
import com.pueblolavanda.singlecards.drivers.device.Writer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

//@RunWith(SpringRunner.class)
//@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class SinglecardsApplicationTests {

	private static final String M_1_NM_XLN_0_ES = "M-1-NM-xln-0-es";
	private static final String XLN = "XLN";
	private static final String NM = "NM";
	private static final String PATH_UMA = "/Users/kojinanjo/Desktop/pictures/uma/";
	private static final String PATH_C18 = "/Users/kojinanjo/Desktop/pictures/c18/";

	private FetchExpansionInteractor interactor;
	private FetchCSVLoaderInteractor csvLoaderInteractor;
	private FetchProductInteractor productsInteractor;
	private FetchSingleInteractor singleInteractor;
	private WebScraper webScraper;

	private UpdatePricesController updateController;


	private List<SingleResponseModel> singleResponseModelList;
	private List<ProductResponseModel> productResponseModelListUpdate;


	@Mock
	private ApiScryfall apiScryfall;
	@Mock
	private ConnPL connPL;

	@Before
	public void setUp() {
		apiScryfall = new ApiScryfall();
		connPL = new ConnPL();
		this.interactor = new FetchExpansionInteractor(apiScryfall);
		this.csvLoaderInteractor = new FetchCSVLoaderInteractor();
		this.productsInteractor = new FetchProductInteractor(connPL);
		this.singleInteractor = new FetchSingleInteractor(apiScryfall);
	}

	@Test
	@Ignore
	public void contextLoads() {
	}


	@Ignore
	@Test
	public void fetchNewPricesTest(){



		Product product = new Product();
		product.setSku(M_1_NM_XLN_0_ES);

		product.setPrecioNormal(new BigDecimal("990"));

		List<Product> productList = new ArrayList<>();
		productList.add(product);

		when(connPL.currentCardInStock()).thenReturn(productList);

		List<ProductResponseModel> productResponseModelList = productsInteractor.getCardsStockPL();
		List<SingleResponseModel> singleResponseModelList = new ArrayList<>();
		for(ProductResponseModel productResponseModel : productResponseModelList) {
			singleResponseModelList.add(singleInteractor.fetch(productResponseModel));
		}


		assertThat(singleResponseModelList.get(0).getSingle().getExpansionCode().toUpperCase(),is(XLN));
		assertThat(singleResponseModelList.get(0).getSingle().getCondition(),is(NM));
		assertThat(singleResponseModelList.get(0).getSingle().getPrice(),is(new BigDecimal("990")));
		assertThat(singleResponseModelList.get(0).getSingle().getCollectorNumber(),is("1"));
		assertThat(singleResponseModelList.get(0).getSingle().isFoil(),is(false));

	}

	//Integration test

	@Test
	public void updatePricesControllerTest(){

			updateController = new UpdatePricesController();
			updateController.updatePriceInPL();

	}


	//TODO: how to comment if a piece of code is for handle errors from client (apiscryfall for example)

	@Test
	public void updatePriceSpecificProduct(){
		List<String> skus = new ArrayList<>();
		skus.add("M-13-NM-uma-0-en");
		skus.add("M-50-NM-uma-0-en");
		skus.add("M-267-NM-c16-0-es");
		skus.add("M-209-NM-c18-0-en");
		skus.add("M-44-NM-c16-0-es");
		updateController = new UpdatePricesController();
		updateController.updatePriceForGivenProduct(skus);
	}

	@Test
	public void test_run() {
		//ExpansionResponseModel expansionModel = interactor.fetch("XLN", "es");
		ExpansionResponseModel expansionModel = interactor.fetch("c17", "en");
		final String path = "/Users/kojinanjo/Desktop/pictures/c17/";
		expansionModel = interactor.removeCard(expansionModel.getExpansion());
		expansionModel = interactor.setAsNotFoil(expansionModel.getExpansion());

		//TODO: round price to 50 or 100
		expansionModel = interactor.fetchCardSitePrice(expansionModel.getExpansion());

		final CSVLoaderResponseModel csvLoaderModel = csvLoaderInteractor.fetchNewCardProducts(expansionModel);
		final String filename = "c17Test_en";

		Writer writer = new Writer();

		try {
			writer.csvWriter(csvLoaderModel,filename);
			writer.downloadImage(path,expansionModel);
		} catch (Exception e) {
			e.printStackTrace();
			errorLangImage(e,expansionModel,writer,path);
		}

	}
	//TODO: correct the 0 if not contain
	@Test
	public void updateDataSEOTest(){

		//TODO: avoid set japanese name, only english, spanish
		ExpansionResponseModel expansionModel = interactor.fetch("m12", "es");
		expansionModel = interactor.removeCard(expansionModel.getExpansion());
		expansionModel = interactor.setAsNotFoil(expansionModel.getExpansion());
		final CSVLoaderResponseModel csvLoaderModel = csvLoaderInteractor.fetchNewCardProducts(expansionModel);

		UpdateDataController updateData = new UpdateDataController();
		updateData.insertSeo(csvLoaderModel);


	}


	@Test
	public void only_downloadIMG(){
		ExpansionResponseModel expansionModel = interactor.fetch("cm2", "en");
		expansionModel = interactor.removeCard(expansionModel.getExpansion());
		expansionModel = interactor.setAsNotFoil(expansionModel.getExpansion());
		//expansionModel = interactor.fetchCardSitePrice(expansionModel.getExpansion());

		final CSVLoaderResponseModel csvLoaderModel = csvLoaderInteractor.fetchNewCardProducts(expansionModel);
		final String filename = "cm2_en";
		final String path = "/Users/kojinanjo/Desktop/pictures/cm2/";

		Writer writer = new Writer();

		try {
			//writer.csvWriter(csvLoaderModel,filename);
			writer.downloadImage(path,expansionModel);
		} catch (Exception e) {
			e.printStackTrace();
			errorLangImage(e,expansionModel,writer,path);
		}
	}

	public void errorLangImage(Exception e, ExpansionResponseModel expansionModel, Writer writer, String path){

		if(e.getMessage().contains("403")){
			expansionModel = interactor.setImageCardInEnglish(expansionModel.getExpansion());
			try {
				writer.downloadImage(path,expansionModel);
			}
			catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}



}
