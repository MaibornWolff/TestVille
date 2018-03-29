"use strict";

class DetailPanelController{

    /* @ngInject */

    /**
     * @external {Timeout} https://docs.angularjs.org/api/ngMock/service/$timeout
     * @constructor
     * @param {Scope} $rootScope
     * @param {Scope} $scope
     * @param {SettingsService} settingsService
     * @param {Timeout} $timeout
     * @param {object} codeMapMaterialFactory
     */
    constructor($rootScope, $scope, settingsService, $timeout, codeMapMaterialFactory){

        /**
         *
         * @type {Object}
         */
        this.mats = codeMapMaterialFactory;

        /**
         * @typedef {object} CommonDetails
         * @property {string} areaAttributeName
         * @property {string} heightAttributeName
         * @property {string} colorAttributeName
         **/

        let commonDetails = {
            areaAttributeName: null,
            heightAttributeName: null,
            colorAttributeName: null
        };

        /**
         * @typedef {object} SpecificDetails
         * @property {string} name
         * @property {number} area
         * @property {number} height
         * @property {number} color
         * @property {number} heightDelta
         * @property {number} areaDelta
         * @property {number} colorDelta
         * @property {string} link
         **/

        let selectedDetails = {
            name: null,
            area: null,
            height: null,
            color: null,
            heightDelta:null,
            areaDelta : null,
            colorDelta : null,
            link:null
        };

        /**
         * @typedef {object} Details
         * @property {CommonDetails} common
         * @property {SpecificDetails} selected
         */

        this.details = {
            common: commonDetails,
            selected:selectedDetails
        };

        /**
         *
         * @type {Scope}
         */
        this.$rootScope = $rootScope;

        /**
         *
         * @type {Scope}
         */
        this.$scope = $scope;

        /**
         *
         * @type {Settings}
         */
        this.settings = settingsService.settings;

        /**
         *
         * @type {Timeout}
         */
        this.$timeout = $timeout;

        let ctx = this;



        // {to: Object3d with node data, from: Object3d with node data}
        ctx.onSelect({from:null, to:null});
        this.$rootScope.$on("building-selected", (e, data) => {
            ctx.onSelect(data);
        });

        // we can use watches here again... we try to keep watches as shallow and small as possible
        this.onSettingsChanged(settingsService.settings);
        $scope.$on("settings-changed", (e, s)=>{ctx.onSettingsChanged(s);});

    }

    /**
     * Called when settings change. Applies them to the common details.
     * @listens {settings-changed}
     * @param {Settings} settings
     */
    onSettingsChanged(settings){
        this.details.common.areaAttributeName = settings.areaMetric;
        this.details.common.heightAttributeName = settings.heightMetric;
        this.details.common.colorAttributeName = settings.colorMetric;
    }

    /**
     * called when a new/no building is selected.
     * @listens {building-selected}
     * @param {object} data
     */
    onSelect(data){

        if (data.to!==null) {
            this.setSelectedDetails(data.to);
        } else {
            this.clearSelectedDetails();

        }
    }





    /**
     * Checks whether a a building is selected
     * @return {boolean}
     */
    isSelected() {
        if(this.details && this.details.selected) {
            return this.details.selected.name ? true : false;

        } else {
            return false;
        }
    }


    /**
     * Sets selected details
     * @param {object} selected selected building
     */
    setSelectedDetails(selected){
        this.$timeout(function() {
            this.details.selected.name        = selected.getName();
            this.details.selected.area        = selected.getAreaMetric();
            this.details.selected.height      = selected.getHeightMetric();
            this.details.selected.color       = selected.getColorMetric();
            this.details.selected.heightDelta = null;
            this.details.selected.areaDelta   = null;
            this.details.selected.colorDelta  = null;
            this.details.selected.link        = selected.link;
        }.bind(this));

    }



    /**
     * clears selected details
     */
    clearSelectedDetails(){
        this.$timeout(function() {
            this.details.selected.name = null;
            this.details.selected.area = null;
            this.details.selected.height = null;
            this.details.selected.color = null;
            this.details.selected.heightDelta = null;
            this.details.selected.areaDelta  = null;
            this.details.selected.colorDelta  = null;
            this.details.selected.link = null;
        }.bind(this));
    }

}

export {DetailPanelController};


