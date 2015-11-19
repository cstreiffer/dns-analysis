import time
import random
import json

weekday = "Fri"
date = "06"
month = "Nov"
year = "2015"
types = ["A", "AAAA", "MX", "NS", "PTR", "SOA", "SRV", "TXT"]
domain_ip_file = "dns_ip_map.txt"
domain_ip_txt = open(domain_ip_file)
domain_ip_list = domain_ip_txt.read().split('\n')

#out_file = open('dns_queries.txt', 'w')
counter = 0
with open('dns_queries.json', 'w') as out_file:
	for i in range(0,10000):
		domip_ind = random.randint(0,len(domain_ip_list)-1)
		domain_ip = domain_ip_list[domip_ind].split(":")
		domain = domain_ip[0].strip()
		ip = domain_ip[1].strip()
		counter+=1
		type_ind = random.randint(0,7)
		dns_type = types[type_ind]
		hour = random.randint(0,23)
		minute = random.randint(0,59)
		second = random.randint(0,59)
		mac_addr = ""
		for j in range(0, 12):
	    		mac_addr = mac_addr+"%x" % random.randint(0, 15) 	
	    		if(j!=11 and (j+1)%2==0):
	    			mac_addr=mac_addr+":"



		datetimestring = weekday+', '+date+' '+month+' '+year+' '+str(hour)+':'+str(minute)+':'+str(second)+' GMT'
		timestamp = time.mktime(time.strptime(datetimestring, '%a, %d %b %Y %H:%M:%S GMT'))
		#print >> out_file, domain+"\t"+ip+"\t"+str(timestamp)+"\t"+dns_type+"\t"+mac_addr
		#test_json_string = domain+"\t"+ip+"\t"+str(timestamp)+"\t"+dns_type+"\t"+mac_addr
		json_data = {}
		json_data['domain'] = domain
		json_data['ip_address'] = ip
		json_data['time'] = timestamp
		json_data['type'] = dns_type
		json_data['MAC'] = mac_addr

		json.dump(json_data, out_file)
		print >> out_file, ""
		print counter

out_file.close()