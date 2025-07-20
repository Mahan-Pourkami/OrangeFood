package Model;

public enum StateofCart {

     accepted,// by restaurant
     rejected , //by restaurant
     served, //ready to give to courier
     received , // by courier
     delivered, //by courier
     waiting,//در انتظار پرداخت
     payed, //پرداخت شده در انتظار رستوران
     acceptedbycourier

}
