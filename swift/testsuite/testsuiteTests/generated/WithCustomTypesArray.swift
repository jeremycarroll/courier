import Foundation
import SwiftyJSON

public struct WithCustomTypesArray: JSONSerializable, Equatable {
    
    public let ints: [Int]?
    
    public let arrays: [[Simple]]?
    
    public let maps: [[String: Simple]]?
    
    public let unions: [WithCustomTypesArrayUnion]?
    
    public let fixed: [String]?
    
    public init(
        ints: [Int]?,
        arrays: [[Simple]]?,
        maps: [[String: Simple]]?,
        unions: [WithCustomTypesArrayUnion]?,
        fixed: [String]?
    ) {
        self.ints = ints
        self.arrays = arrays
        self.maps = maps
        self.unions = unions
        self.fixed = fixed
    }
    
    public static func read(json: JSON) -> WithCustomTypesArray {
        return WithCustomTypesArray(
            ints: json["ints"].array.map { $0.map { $0.intValue } },
            arrays: json["arrays"].array.map { $0.map { $0.arrayValue.map { Simple.read($0.jsonValue) } } },
            maps: json["maps"].array.map { $0.map { $0.dictionaryValue.mapValues { Simple.read($0.jsonValue) } } },
            unions: json["unions"].array.map { $0.map { WithCustomTypesArrayUnion.read($0.jsonValue) } },
            fixed: json["fixed"].array.map { $0.map { $0.stringValue } }
        )
    }
    public func write() -> JSON {
        var json: [String : JSON] = [:]
        if let ints = self.ints {
            json["ints"] = JSON(ints)
        }
        if let arrays = self.arrays {
            json["arrays"] = JSON(arrays.map { JSON($0.map { $0.write() }) })
        }
        if let maps = self.maps {
            json["maps"] = JSON(maps.map { JSON($0.mapValues { $0.write() }) })
        }
        if let unions = self.unions {
            json["unions"] = JSON(unions.map { $0.write() })
        }
        if let fixed = self.fixed {
            json["fixed"] = JSON(fixed.map { JSON($0) })
        }
        return JSON(json)
    }
}
public func ==(lhs: WithCustomTypesArray, rhs: WithCustomTypesArray) -> Bool {
    return (
        (lhs.ints == nil ? (rhs.ints == nil) : lhs.ints! == rhs.ints!) &&
        (lhs.arrays == nil ? (rhs.arrays == nil) : lhs.arrays! == rhs.arrays!) &&
        (lhs.maps == nil ? (rhs.maps == nil) : lhs.maps! == rhs.maps!) &&
        (lhs.unions == nil ? (rhs.unions == nil) : lhs.unions! == rhs.unions!) &&
        (lhs.fixed == nil ? (rhs.fixed == nil) : lhs.fixed! == rhs.fixed!) &&
        true
    )
}