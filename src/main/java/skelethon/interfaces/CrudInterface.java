package skelethon.interfaces;

import Models.BaseModel;

public interface CrudInterface {
    Object post(BaseModel baseModel);
    Object get();
    Object put(BaseModel baseModel);
    Object delete(long id);


}
