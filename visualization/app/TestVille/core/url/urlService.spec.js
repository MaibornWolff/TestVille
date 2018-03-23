require("./url.js");

/**
 * @test {UrlService}
 */
describe("app.testVille.core.url.urlService", function() {

    var urlService, $location, $httpBackend;

    beforeEach(angular.mock.module("app.testVille.core.url"));
    beforeEach(angular.mock.inject(function (_urlService_, _$location_, _$httpBackend_) {
        urlService = _urlService_;
        $location = _$location_;
        $httpBackend = _$httpBackend_;
    }));

    describe("service.getUrl.tests", ()=>{

        it("url should be location's url", ()=>{
            $location.url("somePath.html");
            expect(urlService.getUrl()).to.equal($location.absUrl());
        });

    });



    describe("service.getParameterByName.test", ()=>{


        it("query parameter(s) should be recognized from static url and location mock", () => {

            let invalidParam ="invalid";
            let param1 = "param1";
            let value1 = "value1";
            let param2 = "param2";
            let value2 = "value2";
            let url1 = "http://testurl?"+param1+"="+value1;
            let url2 = "http://testurl?"+param1+"="+value1+"&"+param2+"="+value2;

            $location.url(url1);

            expect(urlService.getParameterByName(param1)).to.equal(value1);
            expect(urlService.getParameterByName(invalidParam)).to.equal(null);

            expect(urlService.getParameterByName(param1, url1)).to.equal(value1);
            expect(urlService.getParameterByName(invalidParam, url1)).to.equal(null);

            $location.url(url2);

            expect(urlService.getParameterByName(param1)).to.equal(value1);
            expect(urlService.getParameterByName(param2)).to.equal(value2);
            expect(urlService.getParameterByName(invalidParam)).to.equal(null);

            expect(urlService.getParameterByName(param1, url2)).to.equal(value1);
            expect(urlService.getParameterByName(param2, url2)).to.equal(value2);
            expect(urlService.getParameterByName(invalidParam, url2)).to.equal(null);

        });

    });

    describe("service.getFileDataFromQueryParam.tests", ()=>{

        it("getFileDataFromQueryParam should allow URL's", (done) => {

            // mocks + values
            let url = "http://testurl.de/?file=http://someurl.com/some.json";

            $httpBackend
                .when("GET", "http://someurl.com/some.json")
                .respond(200, "validData");

            $location.url(url);

            urlService.getFileDataFromQueryParam().then(
                (data) => {
                    expect(data).to.equal("validData");
                    done();
                },() => {
                    done("should succeed");
                }
            );

            $httpBackend.flush();

        });


        it("file parameter should correctly resolve to a file", (done) => {

            // mocks + values
            let url = "http://testurl?file=valid.json";

            $httpBackend
                .when("GET", "valid.json")
                .respond(200, "validData");

            $location.url(url);

            urlService.getFileDataFromQueryParam().then(
                (data) => {
                    expect(data).to.equal("validData");
                    done();
                },() => {
                    done("should succeed");
                }
            );

            $httpBackend.flush();

        });

    });
});

