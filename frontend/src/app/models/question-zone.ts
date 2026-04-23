import {Question} from "./question";
import {ViolenceZoneInterface} from "./zone";



export interface QuestionZone extends Omit<Question, 'id_zone'>{
    zone:ViolenceZoneInterface
}
