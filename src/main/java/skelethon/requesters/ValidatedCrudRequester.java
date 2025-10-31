package skelethon.requesters;

import Models.BaseModel;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import skelethon.EndPoints;
import skelethon.HttpRequester;
import skelethon.interfaces.CrudInterface;
import skelethon.interfaces.SpecialCruds;
import skelethon.interfaces.SpecialExtracts;

import java.util.Arrays;
import java.util.List;

public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequester implements CrudInterface, SpecialExtracts, SpecialCruds {
    private final CrudRequester crudRequester;
    public ValidatedCrudRequester(RequestSpecification requestSpecification, EndPoints endPoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endPoint, responseSpecification);
        this.crudRequester = new CrudRequester(requestSpecification, endPoint, responseSpecification);
    }

    @Override
    public T post(BaseModel baseModel) {
        return (T)crudRequester.post(baseModel).extract().response().as(endPoint.getResponseModel());
    }
    @Override
    public T post() {
        return (T)crudRequester.post().extract().response().as(endPoint.getResponseModel());
    }

    @Override
    public T get() {
        return (T)crudRequester.get().extract().response().as(endPoint.getResponseModel());
    }

    @Override
    public List<T> getList () {
        return Arrays.asList((T[])crudRequester.get().extract().as(endPoint.getResponseModel().arrayType()));
    }

    @Override
    public T put(BaseModel baseModel) {
        return (T)crudRequester.put(baseModel).extract().response().as(endPoint.getResponseModel());
    }

    @Override
    public Object delete(long id) {
        return null;
    }


    @Override
    public String postErorr(BaseModel baseModel) {
        return crudRequester.post(baseModel).extract().response().asString();
    }
}
