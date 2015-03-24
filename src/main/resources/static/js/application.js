var RECORD_API = '/api/records/:recordId';

var main = angular.module("dsr", ["ngResource"]);
var recordModule = angular.module('recordModule', ['ngResource']);
recordModule.service('RecordService', function ($resource, $http) {
    var RecordResource = $resource(RECORD_API);

    this.newEmptyRecord = function () {
        var recordResourceInstance = new RecordResource();
        recordResourceInstance.doctor = {name: '', hospital: ''};
        recordResourceInstance.medicineComments = [
            {medicineName: '', comment: ''}
        ];
        return recordResourceInstance;
    };

    this.save = function (record, scope, dataName) {
        record.$save({}, function () {
            $http.get("/api/records?page=1").success(function (data) {
                pageableDataSetter(data, scope, dataName);
            });
        });
        return this.newEmptyRecord();
    };

    this.deleteRecord = function (id, callback) {
        RecordResource.delete({recordId: id}, function (data) {
            console.log("Delete the visit record " + id + " success");
            callback();
        });
    }
});

var pageableDataSetter = function (data, scope, contentName) {
    scope[contentName] = data.content;
    scope.totalNumber = data.totalElements;
    scope.currentPage = data.number + 1;
    scope.totalPages = data.totalPages;
    scope.range = range(data.totalPages);
};

var range = function (totalPages) {
    var ret = [];
    for (var i = 0; i < totalPages; i++) {
        ret.push(i + 1);
    }
    return ret;
};

recordModule.controller('RecordControl', function ($scope, $http, $resource, RecordService) {
    $http.get("/api/records?page=1").success(function (data) {
        pageableDataSetter(data, $scope, "records");
    });

    $scope.pageChanged = function (pageNumber) {
        $http.get("/api/records?page=" + pageNumber).success(function (data) {
            pageableDataSetter(data, $scope, "records");
        });
    };

    $scope.initVisitRecord = function () {
        $scope.recordResource = RecordService.newEmptyRecord();
        $scope.hospital = [];
        $scope.visitAtDatetime = moment().format('YYYY-MM-DD HH:mm');
    };

    $scope.initVisitRecord();

    $scope.saveVisitRecord = function () {
        // Convert the time of visitAt to a timestamp, complement with '000' to make equal.
        $scope.recordResource.doctor.hospital = $scope.hospital;
        $scope.recordResource = RecordService.save($scope.recordResource, $scope, "records");
        $scope.hospital = null;
    };

    $scope.setDeleteId = function (id) {
        console.log("Delete ID is " + id);
        $scope.deleteId = id;
    };

    $scope.deleteRecord = function (id) {
        console.log("try to delete");
        RecordService.deleteRecord($scope.deleteId, function () {
            $http.get("/api/records?page=1").success(function (data) {
                pageableDataSetter(data, $scope, "records");
            });
        });
    };

    $scope.addComment = function () {
        $scope.recordResource.medicineComments.push({medicineName: '', comment: ''});
    };

    $scope.removeComment = function (comment) {
        for (i = 0; i < $scope.recordResource.medicineComments.length; i++) {
            var currentComment = $scope.recordResource.medicineComments[i];
            if (currentComment == comment) {
                $scope.recordResource.medicineComments.splice(i, 1);
            }
        }
    };

    // Load other resources, may should be remove to factory method.
    var HospitalResource = $resource("/api/hospitals");
    $scope.hospitals = HospitalResource.query();

    $scope.medicines = $resource("/api/medicines").query();


    $scope.queryDoctors = function (hospital) {
        $http.get("/api/hospitals/" + hospital.id + "/doctors").success(function (data) {
            $scope.doctors = data;
            console.log(data);
        });
    };

    $scope.getVisitRecord = function (id) {
        $http.get("/api/records/" + id).success(function (data) {
            $scope.recordResource.id = data.id;
            $scope.recordResource.doctor = data.doctor;
            $scope.recordResource.hospital = data.hospital;
            $scope.recordResource.visitAt = data.visitAt;
            $scope.recordResource.medicineComments = data.medicineComments;

            for (i = 0; i < $scope.hospitals.length; i++) {
                var currentHospital = $scope.hospitals[i];
                if ($scope.recordResource.doctor.hospital.name === currentHospital.name) {
                    $scope.hospital = currentHospital;
                }
            }

            $http.get("/api/hospitals/" + $scope.hospital.id + "/doctors").success(function (data) {
                $scope.doctors = data;
                for (i = 0; i < $scope.doctors.length; i++) {
                    var currentDoctor = $scope.doctors[i];
                    if ($scope.recordResource.doctor.name === currentDoctor.name) {
                        console.log("Find doctor: " + currentDoctor.name);
                        $scope.recordResource.doctor = currentDoctor;
                    }
                }
            });

            for (i = 0; i < $scope.recordResource.medicineComments.length; i++) {
                console.log("Set medicine for " + $scope.recordResource.medicineComments[i].id);
                for (j = 0; j < $scope.medicines.length; j++) {
                    var currentMedicine = $scope.medicines[j];
                    if ($scope.recordResource.medicineComments[i].medicine.name === currentMedicine.name) {
                        $scope.recordResource.medicineComments[i].medicine = currentMedicine;
                        console.log("set medicine as " + currentMedicine.name);
                    }
                }
            }
        });
    }
});

main.controller('UserControl', function ($scope, $http, $resource) {
    var User = $resource("/api/users");
    $scope.user = new User();
    $scope.successNotify = function () {
        $(".alert").show();

        setTimeout(function () {
            $('.alert').hide();
        }, 15000);

        $http.get("/api/users?page=1").success(function (data) {
            pageableDataSetter(data, $scope, "users");
        });
    };

    $http.get("/api/users?page=1").success(function (data) {
        pageableDataSetter(data, $scope, "users");
    });

    $scope.pageChanged = function (pageNumber) {
        $http.get("/api/users?page=" + pageNumber).success(function (data) {
            pageableDataSetter(data, $scope, "users");
        });
    };
});