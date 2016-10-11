package net.skyscanner.cleanarchitecture.presentation.presenter;

import net.skyscanner.cleanarchitecture.domain.usecases.AddItem;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.internal.util.SubscriptionList;

public class AddItemPresenter extends AbstractPresenter<AddItemPresenter.View> {

    private AddItem mAddItem;
    private String mContentText;
    private String mDetailText;
    private SubscriptionList mSubscriptionList = new SubscriptionList();

    public AddItemPresenter(AddItem addItem) {
        mAddItem = addItem;
    }

    @Override
    void onTakeView() {
        mSubscriptionList.add(mView.contentTextChanged().doOnNext(new Action1<String>() {
            @Override
            public void call(String contentText) {
                mContentText = contentText;
            }
        }).zipWith(mView.detailTextChanged().doOnNext(new Action1<String>() {
            @Override
            public void call(String detailText) {
                mDetailText = detailText;
            }
        }), new Func2<String, String, Boolean>() {
            @Override
            public Boolean call(String content, String detail) {
                return content.length() > 0 && detail.length() > 0;
            }
        }).subscribe(mView.setAddButtonEnabled()));

        mSubscriptionList.add(mView.addButtonClicks().flatMap(new Func1<Void, Observable<Void>>() {
            @Override
            public Observable<Void> call(Void aVoid) {
                return mAddItem.execute(mContentText, mDetailText).map(new Func1<String, Void>() {
                    @Override
                    public Void call(String s) {
                        return null;
                    }
                });
            }
        }).subscribe(mView.dismissView()));

        mSubscriptionList.add(mView.cancelButtonClicks().subscribe(mView.dismissView()));
    }

    @Override
    void onDropView() {
        mSubscriptionList.unsubscribe();
    }

    public interface View {
        Observable<String> contentTextChanged();

        Observable<String> detailTextChanged();

        Observable<Void> addButtonClicks();

        Observable<Void> cancelButtonClicks();

        Action1<Boolean> setAddButtonEnabled();

        Action1<Void> dismissView();
    }
}
