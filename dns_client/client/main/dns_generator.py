from subprocess import check_output

domain_file = "domain_list.txt"

domain_txt = open(domain_file)

domain_list = domain_txt.read()

out_file = open('dns_ip_map.txt', 'w')
counter = 0
for line in domain_list.split('\n'):
	counter+=1
	dig_response = check_output(["dig", line])
	dig_test = False
	dig_ip = ""
	for i in range(0, len(dig_response.split('\n'))):
	 	dig_line = dig_response.split('\n')[i]
	 	#print dig_line

	 	if "ANSWER SECTION:" in dig_line:
	 		dig_test = True
	 		continue
	 	elif dig_test:
	 		if dig_line:
	 			dig_ip = dig_line.split()[4].strip()
	 		break
	if dig_ip:
		print counter
		print >> out_file, line+" : "+dig_ip

out_file.close()